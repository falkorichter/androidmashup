<td colspan="4">
    <?php echo link_to($extratype->getIdExtratype() ? $extratype->getIdExtratype() : __('-'), 'extratype/edit?id_extratype='.$extratype->getIdExtratype()) ?>
     - 
    <?php echo $extratype->getName() ?>
     - 
    <?php echo $extratype->getDescription() ?>
     - 
    <?php echo $extratype->getJavaRetrieveCommand() ?>
     - 
</td>