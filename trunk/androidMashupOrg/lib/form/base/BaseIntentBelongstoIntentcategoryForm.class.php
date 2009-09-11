<?php

/**
 * IntentBelongstoIntentcategory form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseIntentBelongstoIntentcategoryForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_intent_belongsTo_intentCategory' => new sfWidgetFormInputHidden(),
      'intent_id'                          => new sfWidgetFormPropelChoice(array('model' => 'Intent', 'add_empty' => true)),
      'intentCategory_id'                  => new sfWidgetFormPropelChoice(array('model' => 'Intentcategory', 'add_empty' => true)),
    ));

    $this->setValidators(array(
      'id_intent_belongsTo_intentCategory' => new sfValidatorPropelChoice(array('model' => 'IntentBelongstoIntentcategory', 'column' => 'id_intent_belongsTo_intentCategory', 'required' => false)),
      'intent_id'                          => new sfValidatorPropelChoice(array('model' => 'Intent', 'column' => 'id_intent', 'required' => false)),
      'intentCategory_id'                  => new sfValidatorPropelChoice(array('model' => 'Intentcategory', 'column' => 'id_intentCategory', 'required' => false)),
    ));

    $this->widgetSchema->setNameFormat('intent_belongsto_intentcategory[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'IntentBelongstoIntentcategory';
  }


}
