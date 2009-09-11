<?php

/**
 * IntentHandlesExtra form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseIntentHandlesExtraForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_intent_handles_extra' => new sfWidgetFormInputHidden(),
      'intent_id'               => new sfWidgetFormPropelChoice(array('model' => 'Intent', 'add_empty' => true)),
      'extra_id'                => new sfWidgetFormPropelChoice(array('model' => 'Extra', 'add_empty' => true)),
      'mandatory'               => new sfWidgetFormInput(),
    ));

    $this->setValidators(array(
      'id_intent_handles_extra' => new sfValidatorPropelChoice(array('model' => 'IntentHandlesExtra', 'column' => 'id_intent_handles_extra', 'required' => false)),
      'intent_id'               => new sfValidatorPropelChoice(array('model' => 'Intent', 'column' => 'id_intent', 'required' => false)),
      'extra_id'                => new sfValidatorPropelChoice(array('model' => 'Extra', 'column' => 'id_extra', 'required' => false)),
      'mandatory'               => new sfValidatorString(array('max_length' => 45, 'required' => false)),
    ));

    $this->widgetSchema->setNameFormat('intent_handles_extra[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'IntentHandlesExtra';
  }


}
