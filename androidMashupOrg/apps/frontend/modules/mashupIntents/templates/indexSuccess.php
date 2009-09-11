<h1>Available Mashup Intents:</h1>
<?
foreach ( $mashupIntents as $mashupIntent ) {?>
	<h2><?= $mashupIntent->getTitle();?></h2>
	<p>Intent Action: <?=$mashupIntent->getAction()?></p>
	<p>Title:	<?=  $mashupIntent->getTitle();?></p>
	<p>Description: </p>
	<p><?= $mashupIntent->getDescription();?></p>
	
	<? $extras = $mashupIntent->getExtras ();?>
	<h3>required Extras for this intent:</h3>
	<ul>
	<? foreach ( $extras as $extra ) {?>
		<? if ($extra->isMandatory() == "true"){ ?>
		<li>
			key: <?=  $extra->getName ();?></br>
			type:<?=  $extra->getExtratype ()->getName ();?> (<?=  $extra->getExtratype ()->getDescription ();?> )</br>
			<?= $extra->getRelationDescription ();?>
			
		<? }?>
		</li>
	<? }?>
	</ul>
	<h3>optional Extras for this intent:</h3>
	<ul>
	<? foreach ( $extras as $extra ) {?>
	<? if ($extra->isMandatory () == "false"){ ?>
		<li>
			key: <?=  $extra->getName ();?></br>
			type:<?=  $extra->getExtratype ()->getName ();?> (<?=  $extra->getExtratype ()->getDescription ();?> )</br>
			<?= $extra->getRelationDescription ();?>
		<? }?>
		</li>
	<? }?>
	</ul>
	<h3>Sample Implementation:</h3>
	for your manifest:</br>
	<blockquote>  	  	
		&lt;intent-filter&gt; </br>
				&lt;action android:name="<?=$mashupIntent->getAction()?>" /&gt; </br>
				&lt;category android:name="android.intent.category.DEFAULT" /&gt; </br>
			&lt;/intent-filter&gt; </br>
	</blockquote>
	to generate the Intent:
	<blockquote>  	  	
		i = new Intent("<?=$mashupIntent->getAction()?>");</br>
		<? foreach ( $extras as $extra ) {?>
			i.putExtra("<?=  $extra->getName ();?>", "sample");</br>
		<? }?>
	</blockquote>
	to retrieve data from this Intent:
	<blockquote>
	  	 <? foreach ( $extras as $extra ) {?>
			<?= $extra->getExtratype ()->getName ();?> <?=  $extra->getName ()?> = 
			<?=  $extra->getExtratype ()->getJavaRetrieveCommand ()?>("<?=  $extra->getName ();?>", DEFAULT_VALUE);</br>
		<? }?> 	
		
	</blockquote>
<?	}?>





